/*
 * Copyright 2021 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.fabricmc.loom.nativesupport.mixin;

import ca.weblite.objc.RuntimeUtils;
import net.minecraft.client.util.MacWindowUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MacWindowUtil.class)
public abstract class MacWindowUtilMixin {
	private static final Logger LOGGER = LogManager.getLogger("MacWindowUtilMixin");

	@Shadow
	@Final
	private static int FULLSCREEN_MASK;

	@Inject(method = "toggleFullscreen(J)V", at = @At("HEAD"), cancellable = true)
	private static void toggleFullscreen(long handle, CallbackInfo ci) {
		try {
			// First see if it loads, if so there is nothing else for us to do here.
			if (RuntimeUtils.loaded) {
				return;
			}
		} catch (Throwable e) {
			LOGGER.error("Failed to load java-objc-bridge, catching error", e);
		}

		// This fixes going into full screen, but crashes when exiting, seems to be unsupported on MacOS 11?
		//		long windowPtr = GLFWNativeCocoa.glfwGetCocoaWindow(handle);
		//		if (windowPtr != 0) {
		//			long objc_msgSend = ObjCRuntime.getLibrary().getFunctionAddress("objc_msgSend");
		//			long styleMask = JNI.invokePPP(windowPtr, sel_getUid("styleMask"), objc_msgSend);
		//			boolean isFullscreen = (styleMask & FULLSCREEN_MASK) == FULLSCREEN_MASK;
		//			if (!isFullscreen) {
		//				JNI.invokePPP(windowPtr, sel_getUid("toggleFullScreen:"), objc_msgSend);
		//			}
		//		}

		// Give up for now and skip trying to enter the special MacOS fullscreen mode.
		// The game will still be fullscreen just not as a MacOS style fullscreen application.
		ci.cancel();
	}
}
