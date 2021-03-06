/*
 * Copyright (c) 2016 Andre Tietz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.andretietz.retroauth;

import java.io.IOException;

/**
 * This Exception is thrown, when the user cancels the Authentication or
 * some other error happens. The Reason can be read, on calling {@link AuthenticationCanceledException#getCause()}
 */
public class AuthenticationCanceledException extends IOException {
    public AuthenticationCanceledException(String detailMessage) {
        super(detailMessage);
    }

    public AuthenticationCanceledException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public AuthenticationCanceledException(Throwable throwable) {
        super(throwable);
    }
}
