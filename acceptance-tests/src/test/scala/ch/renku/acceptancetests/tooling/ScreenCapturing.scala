/*
 * Copyright 2021 Swiss Data Science Center (SDSC)
 * A partnership between École Polytechnique Fédérale de Lausanne (EPFL) and
 * Eidgenössische Technische Hochschule Zürich (ETHZ).
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

package ch.renku.acceptancetests.tooling

import java.io.File
import java.nio.file.Paths
import java.time.LocalDateTime.now
import java.time.format.DateTimeFormatter.ofPattern

trait ScreenCapturing {
  self: AcceptanceSpec =>

  /** Public helper function to write a screenshot of the current browser state.
    */
  def saveScreenshot(): Unit = {
    val captureDir = Paths.get("target")
    browser.setCaptureDir(captureDir.toFile.getAbsolutePath)

    val file = s"${now() format ofPattern("yyyyMMddHHmmss")}-${getClass.getSimpleName}.png"
    println(s"Screenshot saved to ${captureDir.toFile.getAbsolutePath}${File.separator}$file")
    browser.capture.to(file)(webDriver)
  }
}
