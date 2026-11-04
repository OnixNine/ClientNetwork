// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 OnixNine
package me.onixdev.ircchat.util.config

import me.onixdev.ircchat.service.auth.Hash

data class BaseConfig(val port: Int, val timeout: Int, val hash: Hash)
