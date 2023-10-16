/**
 * Copyright (C) 2009, Progress Software Corporation and/or its
 * subsidiaries or affiliates. All rights reserved.
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
package com.tngtech.jgiven.impl.util;

import static org.fusesource.jansi.internal.CLibrary.STDOUT_FILENO;
import static org.fusesource.jansi.internal.CLibrary.isatty;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import org.fusesource.jansi.AnsiColors;
import org.fusesource.jansi.AnsiMode;
import org.fusesource.jansi.AnsiType;
import org.fusesource.jansi.io.AnsiOutputStream;
import org.fusesource.jansi.io.AnsiProcessor;
import org.fusesource.jansi.io.WindowsAnsiProcessor;

/**
 * This is actually a modified copy of AnsiConsole from the jansi project.
 * We had to copy it, because we want to be able to disable the tty detection.
 */
public class AnsiUtil {
    public static OutputStream wrapOutputStream( final OutputStream stream, boolean ttyDetection ) {

        String os = System.getProperty( "os.name" );
        String vendor= System.getProperty( "java.vendor" );
        if( os.startsWith( "Windows" ) ) {

            // On windows we know the console does not interpret ANSI codes..
            try {
                return getAnsiOutputStream(stream, new WindowsAnsiProcessor(stream, false), AnsiType.Emulation,
                        AnsiMode.Default);
            } catch( Throwable ignore ) {
                // this happens when JNA is not in the path.. or
                // this happens when the stdout is being redirected to a file.
            }

            // Use the ANSIOutputStream to strip out the ANSI escape sequences.
            return getAnsiOutputStream(stream, null, AnsiType.Emulation,AnsiMode.Default);
        }
        if( vendor.toLowerCase().contains( "android" ) ) {
            //todo is this ok?
            return new FilterOutputStream( stream ) {
                @Override
                public void close() throws IOException {
                    write( AnsiOutputStream.RESET_CODE );
                    flush();
                    super.close();
                }
            };
        }
        if( !vendor.toLowerCase().contains( "android" )&&ttyDetection ) {
            // We must be on some unix variant..
            try {
                // If we can detect that stdout is not a tty.. then setup
                // to strip the ANSI sequences..
                int rc = isatty( STDOUT_FILENO );
                if( rc == 0 ) {
                    return getAnsiOutputStream(stream, null,  AnsiType.Native,AnsiMode.Strip);
                }

                // These erros happen if the JNI lib is not available for your platform.
            } catch( NoClassDefFoundError ignore ) {} catch( UnsatisfiedLinkError ignore ) {}
        }

        // By default we assume your Unix tty can handle ANSI codes.
        // Just wrap it up so that when we get closed, we reset the
        // attributes.
        return new FilterOutputStream( stream ) {
            @Override
            public void close() throws IOException {
                write( AnsiOutputStream.RESET_CODE );
                flush();
                super.close();
            }
        };
    }

    private static AnsiOutputStream getAnsiOutputStream(OutputStream stream, AnsiProcessor processor, AnsiType type,
            AnsiMode mode) {
        return new AnsiOutputStream(
                stream,
                null,
                mode,
                processor,
                type,
                AnsiColors.TrueColor,
                StandardCharsets.UTF_8,
                null,
                null,
                false);
    }
}
