{
  pkgs,
  renku-ssh-proxy,
  renku-ssh-proxy-no_default_features,
  ...
}:
{
  inherit renku-ssh-proxy renku-ssh-proxy-no_default_features;
  default = renku-ssh-proxy;

  docker = pkgs.dockerTools.buildImage {
    name = "renku-ssh-proxy";
    tag = "musl";
    copyToRoot = pkgs.buildEnv {
      name = "image-root";
      paths = [
        pkgs.bash
        pkgs.iputils
        pkgs.inetutils
        pkgs.openssh
        renku-ssh-proxy
      ];
      pathsToLink = [ "/bin" "/sbin" ];
    };
    config.Entrypoint = [ (pkgs.lib.getExe renku-ssh-proxy) ];
  };

  sessionimage =
    let
      page = pkgs.writeTextDir "index.html" ''
        <!doctype html>
        <html><body><h1>Hello world!</h1></body></html>
      '';
    in
    pkgs.dockerTools.buildImage {
    name = "session-test";
    tag = "latest";
    copyToRoot = pkgs.buildEnv {
      name = "image-root";
      pathsToLink = [ "/bin" "/sbin" ];
      paths = [
        pkgs.bash
        pkgs.iproute2
        pkgs.iputils
        pkgs.inetutils
        pkgs.net-tools
        pkgs.snitch
        pkgs.openssh
        pkgs.procps
        pkgs.curl
        pkgs.coreutils
        page
      ];
    };
    config.Entrypoint = [ "${pkgs.busybox}/bin/httpd" "-f" "-p" "8888" "-h" "${page}"  ];
  };
}
