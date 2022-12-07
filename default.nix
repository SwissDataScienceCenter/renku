{ system ? builtins.currentSystem }:
let
  pkgs = import <nixpkgs> { inherit system; };
  mach-nix = import (builtins.fetchGit {
    url = "https://github.com/DavHau/mach-nix";
    ref = "refs/tags/3.5.0";
    # ref = "master";
  }) {
    pypiDataRev = "982b6cdf6552fb9296e1ade29cf65a2818cbbd6b";
    pypiDataSha256 = "sha256:166y0li0namv6a8ik8qq79ibck4w74x0wgypn9r7sqbb2wvcvcf3";
    python = "python39";
  };
  myhunspell = (pkgs.hunspellWithDicts (with pkgs.hunspellDicts; [en-us]));

  # Note: ideally we would install renku from ./docs/renku-python directly
  # but currently this doesn't seem trivial to do so we install renku
  # from PyPi assuming that the latest published version is anyway what is
  # usually needed.

  python-packages = mach-nix.mkPython {
    requirements = pkgs.lib.strings.concatStringsSep "\n" (
      pkgs.lib.lists.remove "./docs/renku-python" (
        pkgs.lib.strings.splitString "\n" (
          builtins.readFile ./docs/requirements.txt
        )
      ) ++ [
          "renku"
          "lockfile"
          "pip"
          "chartpress==0.7.0"
        ]
    );

    providers = {
    _default = "wheel,sdist,nixpkgs";
    psutil = "sdist,nixpkgs";
    pyenchant = "nixpkgs";
    };
  };
in with pkgs;
  mkShell {
    name = "renku-dev-env";
    shellHook =
    ''
      git submodule sync
      git submodule update --init --force docs/renku-python
    '';
    buildInputs =
    [
      git
      git-lfs
      graphviz
      kubernetes-helm
      myhunspell
      python-packages
    ];
  }
