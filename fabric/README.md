# IRC Chat — Fabric Test Client (`fabric`)

Отдельный Gradle-модуль: клиентский мод для **Fabric 1.20.5 (Yarn)**, который
используется как тестовый клиент протокола IRC-чата (`:protocol`).

Мод ничего не добавляет в игровой мир — он просто умеет подключаться к серверу
`IrcChat` по его сетевому протоколу и обмениваться пакетами, а результат
показывает в чате Minecraft.

## Что внутри

| Файл | Назначение |
|------|------------|
| `IrcTestMod` | точка входа (`client` entrypoint), команда `/irctest ...` |
| `IrcConnection` | обёртка над `NetworkClient`: connect / auth / send / disconnect |
| `IrcPacketHandler` | обработчики входящих пакетов сервера |
| `IrcClientConfig` | конфиг `.minecraft/config/ircchat-test.json` |

Регистрация пакетов повторяет серверную (`Server.createPacketRegistry()`):

| id  | пакет | направление |
|-----|-------|-------------|
| 0   | `AuthRequestPacket` | клиент → сервер |
| 2   | `ClientChatMessagePacket` | клиент → сервер |
| 101 | `AuthResultPacket` | сервер → клиент |
| 102 | `ChatBroadcastPacket` | сервер → клиент |
| 103 | `KeyExchangePacket` | сервер → клиент |

Классы протокола (`ru.kseonyt.net.*` и `me.onixdev.ircchat.impl.*`) берутся из
`:protocol:shadowJar`, где netty уже перепакован в `dev.test.netty`, поэтому
конфликта с netty, встроенным в Minecraft, нет. На этапе сборки fat-jar
протокола вливается прямо в jar мода.

## Как запустить

1. Собери и запусти сервер (корневой модуль):

   ```bash
   ./gradlew run          # или запуск Main из IDE; см. config.json
   ```

   В `config.json` корня лежат `port`, `aes_key` (Base64, AES-128) и `hash`.

2. Запусти клиент с модом:

   ```bash
   ./gradlew :fabric:runClient
   ```

3. Собери готовый mod-jar (для внешнего `.minecraft`):

   ```bash
   ./gradlew :fabric:build
   # результат: fabric/build/libs/ircchat-test-1.0-SNAPSHOT.jar
   ```

   Для обычного запуска Fabric-Loader этот jar уже содержит классы протокола,
   но требует установленных Fabric API и Fabric Loader ≥ 0.15.

## Версии и требования

| Компонент | Версия |
|-----------|--------|
| Gradle    | 9.8.2 |
| Java      | 21 (компиляция мода и MC 1.20.5) |
| Minecraft | 1.20.5 |
| Yarn      | 1.20.5+build.1 |
| Fabric Loader | 0.16.14 |
| Fabric API | 0.97.8+1.20.5 |
| Fabric Loom | 1.17.21 (последний под Java 21) |
| foojay-resolver-convention | 1.0.0 |

> Важно: плагин `foojay-resolver-convention` в `settings.gradle.kts` должен быть
> **≥ 1.0.0**. В версии 0.5.0 он тянет JGit → Gson 2.9.1, который перекрывает
> Gson, нужный Fabric Loom, и сборка падает с
> `Unexpected IllegalAccessException ... (Gson 2.9.1)`.
>
> Loom 1.18+ требует JDK 25 для процесса Gradle. Мы используем 1.17.21, чтобы
> остаться на Java 21 (как и сам Minecraft 1.20.5).

## Конфиг

При первом запуске создаётся `.minecraft/config/ircchat-test.json`:

```json
{
  "host": "127.0.0.1",
  "port": 4847,
  "sender": "fabric-mod",
  "username": "FabricTester",
  "password": "test123",
  "aesKeyBase64": "",
  "compressThreshold": 256,
  "autoConnect": false
}
```

* `aesKeyBase64` — должен совпадать с `aes_key` из серверного `config.json`.
  Если сервер запущен с шифрованием (по умолчанию — да), без него подключение
  не пройдёт. Пустая строка = без шифрования.
* `compressThreshold` — должен совпадать с сервером (там `compress(256)`).
* `autoConnect: true` — подключаться сразу при старте клиента.

## Команды (в игре)

```
/irctest connect            — подключиться и аутентифицироваться (по конфигу)
/irctest auth               — повторно отправить AuthRequestPacket
/irctest disconnect         — закрыть соединение
/irctest status             — состояние (host, connected, authenticated, user)
/irctest send <сообщение>   — отправить сообщение на сервер
/irctest reload             — перечитать конфиг с диска
```

Входящие сообщения от сервера (`ChatBroadcastPacket`) выводятся в чат как
`[IRC] <ник> текст`, ответы авторизации и key exchange — тоже.

## Порядок работы протокола (для теста)

1. Клиент подключается TCP + AES-128-GCM, если задан ключ; включает сжатие.
2. Шлёт `AuthRequestPacket(sender, username, password)`.
3. Сервер отвечает `AuthResultPacket` (`100` — вход, `101` — регистрация,
   `201` — неверный пароль) и `KeyExchangePacket` с серверным AES-ключом.
4. После успешной авторизации клиент может слать `ClientChatMessagePacket`,
   а сервер рассылает `ChatBroadcastPacket` всем авторизованным.

## Заметка про standalone-запуск

Для игры вне dev-окружения (обычный Launcher) mod-jar содержит протокол и
перепакованный netty, так что дополнительно класть `nettyLib.jar` не нужно.
