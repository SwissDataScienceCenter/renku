/*
 * Copyright 2024 Swiss Data Science Center (SDSC)
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

package ch.renku.acceptancetests.model

import org.http4s.AuthScheme.Bearer
import org.http4s.Credentials.Token
import org.http4s.Header
import org.http4s.headers.Authorization
import org.typelevel.ci._

sealed trait AuthorizationToken extends Product with Serializable {
  val value: String
  def asHeader: Header.ToRaw
}

object AuthorizationToken {

  final case class OAuthAccessToken(value: String) extends AuthorizationToken {
    override lazy val asHeader: Header.ToRaw = Authorization(Token(Bearer, value))
  }

  final case class PersonalAccessToken(value: String) extends AuthorizationToken {
    override def asHeader: Header.ToRaw = Header.Raw(ci"PRIVATE-TOKEN", value)
  }
}
