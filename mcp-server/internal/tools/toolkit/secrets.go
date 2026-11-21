package toolkit

import (
	"fmt"
	"net/url"
	"slices"
	"strings"
)

// secretOptions are rclone configuration options that carry a credential. Tools refuse them, so
// that credentials are not sent through the MCP server, where they would end up in the
// conversation and in the logs; the user adds them in the Renku UI.
var secretOptions = map[string]bool{
	"access_grant":                true,
	"access_key_id":               true,
	"apple_id":                    true,
	"authorization":               true,
	"bearer_token":                true,
	"client_access_token":         true,
	"client_id":                   true,
	"client_refresh_token":        true,
	"client_salted_key_pass":      true,
	"client_secret":               true,
	"client_uid":                  true,
	"connection_string":           true,
	"cookies":                     true,
	"drive_id":                    true,
	"impersonate":                 true,
	"key":                         true,
	"key_file_pass":               true,
	"key_pem":                     true,
	"library_key":                 true,
	"link_password":               true,
	"msi_client_id":               true,
	"msi_mi_res_id":               true,
	"msi_object_id":               true,
	"pass":                        true,
	"plex_username":               true,
	"private_access_key":          true,
	"resource_key":                true,
	"root_folder_id":              true,
	"sas_url":                     true,
	"secret_access_key":           true,
	"service_account_credentials": true,
	"session_token":               true,
	"sse_customer_key":            true,
	"sse_customer_key_base64":     true,
	"sse_customer_key_md5":        true,
	"sse_kms_key_id":              true,
	"team_drive":                  true,
	"tenant":                      true,
	"token":                       true,
	"user":                        true,
	"user_id":                     true,
	"username":                    true,
}

// secretKeyParts catch secret-looking keys that are not rclone options, such as a field invented by
// the model. Keys ending in secretKeyAllowedSuffixes name endpoints or files, not secrets.
var (
	secretKeyParts           = []string{"password", "passwd", "secret", "credential", "passphrase", "api_key", "private_key", "token"}
	secretKeyAllowedSuffixes = []string{"_file", "_path", "_url"}
)

// isSecretKey reports whether a configuration key carries a credential.
func isSecretKey(key string) bool {
	name := strings.ToLower(strings.TrimSpace(key))
	if secretOptions[name] {
		return true
	}
	if slices.ContainsFunc(secretKeyAllowedSuffixes, func(s string) bool { return strings.HasSuffix(name, s) }) {
		return false
	}
	return slices.ContainsFunc(secretKeyParts, func(p string) bool { return strings.Contains(name, p) })
}

// carriesValue reports whether v can hold a secret. Empty values are fields the user fills in later,
// and booleans, such as ask_password, are flags.
func carriesValue(v any) bool {
	switch v := v.(type) {
	case nil, bool:
		return false
	case string:
		return v != ""
	}
	return true
}

// hasURLPassword reports whether v is a URL with a password, such as https://user:pass@host.
func hasURLPassword(v any) bool {
	s, ok := v.(string)
	if !ok || !strings.Contains(s, "@") {
		return false
	}
	u, err := url.Parse(s)
	if err != nil || u.User == nil {
		return false
	}
	_, set := u.User.Password()
	return set
}

// SecretKeys returns the dotted paths of the values in v, decoded JSON, that look like credentials:
// a non-empty value under a secret key, or a URL with a password.
func SecretKeys(v any) []string {
	var found []string
	redact(v, "", &found)
	slices.Sort(found)
	return found
}

// RedactSecrets returns a copy of v, decoded JSON, in which the values SecretKeys finds are replaced
// with "[redacted]".
func RedactSecrets(v any) any {
	return redact(v, "", nil)
}

// redact is the single walk behind SecretKeys and RedactSecrets, so that what is refused and what is
// redacted cannot drift apart. It returns a copy of v, at path, with the credentials replaced, and
// appends their paths to found unless found is nil.
func redact(v any, path string, found *[]string) any {
	switch v := v.(type) {
	case map[string]any:
		out := make(map[string]any, len(v))
		for key, child := range v {
			here := key
			if path != "" {
				here = path + "." + key
			}
			if isSecretKey(key) && carriesValue(child) {
				out[key] = redacted(here, found)
			} else {
				out[key] = redact(child, here, found)
			}
		}
		return out
	case []any:
		out := make([]any, len(v))
		for i, child := range v {
			out[i] = redact(child, fmt.Sprintf("%s[%d]", path, i), found)
		}
		return out
	default:
		if hasURLPassword(v) {
			return redacted(path, found)
		}
		return v
	}
}

// redacted records the credential at path in found, unless found is nil, and returns its replacement.
func redacted(path string, found *[]string) string {
	if found != nil {
		*found = append(*found, path)
	}
	return "[redacted]"
}

// RejectSecrets returns a tool error naming the credentials in v, decoded JSON, or nil if there are
// none. what describes v in the message, e.g. "storage".
func RejectSecrets(v any, what string) error {
	keys := SecretKeys(v)
	if len(keys) == 0 {
		return nil
	}
	return fmt.Errorf("refusing to send credentials to the Renku API: %s contains %s. Credentials must "+
		"never be passed as tool arguments, as they end up in the conversation and in logs. Leave them "+
		"out, then tell the user to add them to the data connector in the Renku UI", what, strings.Join(keys, ", "))
}
