{
  description = "Expose renku sessions via SSH";

  outputs =
    { self, ... }@args:
    let
      inputs = (import ./.tack) {
        overrides = args.tackOverrides or { };
      };

      systems = [
        "x86_64-linux"
        "aarch64-linux"
        "aarch64-darwin"
      ];

      eachSystem =
        f:
        (builtins.foldl' (
          acc: system:
          let
            fSystem = f system;
          in
          builtins.foldl' (
            acc': attr:
            acc'
            // {
              ${attr} = (acc'.${attr} or { }) // fSystem.${attr};
            }
          ) acc (builtins.attrNames fSystem)
        ) { } systems);
    in
    {
      overlays = {
        default = self.overlays.renku-ssh-proxy;
        renku-ssh-proxy = _: prev: {
          renku-ssh-proxy = self.packages.${prev.stdenv.hostPlatform.system}.default;
        };
      };
    }
    // eachSystem (
      system:
      let
        pkgs = import inputs.nixpkgs {
          inherit system;
          overlays = [ (import inputs.rust-overlay) ];
        };

        inherit
          (import ./nix {
            inherit
              system
              pkgs
              ;
          })
          packages
          checks
          shell
          ;

        inherit (pkgs) lib;
      in
      {
        packages.${system} = packages;

        apps.${system}.default =
          let
            renku-ssh-proxy = self.packages.${system}.default;
          in
          {
            type = "app";
            program = lib.getExe renku-ssh-proxy;
            inherit (renku-ssh-proxy) meta;
          };

        checks.${system} = checks;

        devShells.${system}.default = shell;
      }
    );
}
