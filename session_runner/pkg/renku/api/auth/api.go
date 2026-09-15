//go:generate go tool github.com/oapi-codegen/oapi-codegen/v2/cmd/oapi-codegen -generate types,client,spec -package auth -o api_gen.go api.spec.yaml

package auth
