{
  description = "renku acceptance tests flake";
  inputs = {
    nixpkgs.url = "nixpkgs/nixos-23.05";
  };

  outputs = { self, nixpkgs }:
    let
      supportedSystems = [ "x86_64-linux" "aarch64-linux" "x86_64-darwin" ];
      forAllSystems = nixpkgs.lib.genAttrs supportedSystems;

      # renkuDeps = pkgs: with pkgs.python3Packages;
      #   [ pip zconfig wcwidth
      #     termcolor shellescape pytz prefixed
      #   ];
    in
    rec
    {
      devShells = forAllSystems(system:
        { default =
            let
              pkgs = import nixpkgs { inherit system; };
            in
              pkgs.mkShell {
                buildInputs = [
                  pkgs.sbt
                  pkgs.openjdk
                  pkgs.chromedriver
                ];

                CHROME_DRIVER = "${pkgs.chromedriver}/bin/chromedriver";

                nativeBuildInputs =
                  [
                  ];
              };
        });
    };
}
