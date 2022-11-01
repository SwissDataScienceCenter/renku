{ description = "Development environment for renku-graph";

  # Install direnv and run `direnv allow` to automatically drop into
  # this environment when entering the directory in your shell.
  # Alternatively, run `nix develop` to drop into a bash shell.
  #
  # Look for packages here:
  # https://search.nixos.org/packages?channel=22.05

  inputs = {
    nixpkgs.url = "nixpkgs/nixos-22.05";
    utils.url = "github:numtide/flake-utils";
  };

  outputs = { self, nixpkgs, utils, ... }:
    utils.lib.eachDefaultSystem (system:
      let
        #overlays = import ./nix/overlays.nix;
        pkgs = import nixpkgs {
          inherit system;
          overlays = [
          ];
        };
      in
        { devShell = pkgs.mkShell {
            buildInputs = with pkgs;
              [ openjdk17
                sbt
                chromedriver
              ];

            JAVA_HOME = "${pkgs.openjdk17}/lib/openjdk";
            CHROME_DRIVER = "${pkgs.chromedriver}/bin/chromedriver";
          };
        }
    );
}
