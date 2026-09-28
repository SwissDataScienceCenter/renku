package commands

import "github.com/spf13/cobra"

func RootCmd() *cobra.Command {
	rootCmd := &cobra.Command{
		Use: "renku_session_runner",
	}
	startCmd := StartCmd()
	rootCmd.AddCommand(startCmd)
	return rootCmd
}
