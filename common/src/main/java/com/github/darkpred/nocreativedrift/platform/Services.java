package com.github.darkpred.nocreativedrift.platform;

import com.github.darkpred.nocreativedrift.DriftUtil;
import com.github.darkpred.nocreativedrift.NoCreativeDriftMod;
import com.github.darkpred.nocreativedrift.ClientConfig;
import org.jetbrains.annotations.ApiStatus;

import java.util.ServiceLoader;

@ApiStatus.Internal
public class Services {

    public static final ClientConfig CONFIG = load(ClientConfig.class);
    public static final DriftUtil DRIFT_UTIL = load(DriftUtil.class);

    public static <T> T load(Class<T> clazz) {

        final T loadedService = ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        NoCreativeDriftMod.LOGGER.debug("Loaded {} for service {}", loadedService, clazz);
        return loadedService;
    }
}
