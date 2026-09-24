package commands

import "github.com/spf13/cobra"

func RootCmd() *cobra.Command {
	rootCmd := &cobra.Command{
		Use: "renku_session_runner",
	}
	registerCmd := StartCmd()
	rootCmd.AddCommand(registerCmd)
	return rootCmd
}
