{ pkgs, ... }:
let
  mach-nix = import (builtins.fetchGit {
    url = "https://github.com/DavHau/mach-nix";
    ref = "refs/tags/3.5.0";
    # ref = "master";
  }) {
    pypiDataRev = "982b6cdf6552fb9296e1ade29cf65a2818cbbd6b";
    pypiDataSha256 = "sha256:166y0li0namv6a8ik8qq79ibck4w74x0wgypn9r7sqbb2wvcvcf3";
    python = "python39";
  };
  python-packages = mach-nix.mkPython {
    requirements = ''
      pip
      powerline-status
      psutil
      setuptools_rust
      virtualenv
      plantweb==1.2.1
      sphinx>=4.1
      sphinxcontrib-mermaid==0.7.1
      sphinxcontrib-napoleon==0.7
      sphinx-click==4.3.0
      sphinx-copybutton==0.5.1
      sphinx-panels
      renku-sphinx-theme==0.2.2
      sphinxcontrib-spelling==7.*
      sphinxcontrib-websupport==1.2.4
      pyenchant
      renku
      lockfile
    '';
    providers = {
    _default = "wheel,sdist,nixpkgs";
    psutil = "sdist,nixpkgs";
    pyenchant = "nixpkgs";
    };

    # packagesExtra = [ mach-nix.buildPythonPackage {
    #   src = ./docs/renku-python;
    #   requirementsExtra = "lockfile";
    #   }
    # ];
    _.pyenchant.nativeBuildInputs.mod = pySelf: self: oldVal: oldVal ++ [pkgs.hunspellDicts.en-us];
};
in
{
  # https://devenv.sh/basics/
  env.GREET = "devenv";

  # https://devenv.sh/packages/
  packages = [
    pkgs.git
    pkgs.kubernetes-helm
    python-packages
    pkgs.hunspell
    pkgs.hunspellDicts.en-us
    ];

  enterShell = ''
    hello
    git --version
    echo "helm version"
    helm version
    echo renku v$(renku --version)
  '';

  # https://devenv.sh/languages/
  languages.nix.enable = true;

  # https://devenv.sh/scripts/
  scripts.hello.exec = "echo hello from $GREET";

  # https://devenv.sh/pre-commit-hooks/
  pre-commit.hooks.shellcheck.enable = true;

  # https://devenv.sh/processes/
  # processes.ping.exec = "ping example.com";

  # devcontainer
  devcontainer.enable = true;
}
