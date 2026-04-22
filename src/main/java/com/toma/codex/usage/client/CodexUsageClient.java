package com.toma.codex.usage.client;

import com.toma.codex.usage.model.UsageSnapshot;

public interface CodexUsageClient {
	UsageSnapshot fetchUsage();
}
