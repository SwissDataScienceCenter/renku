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
    config.Entrypoint = [ (pkgs.lib.getExe renku-ssh-proxy) ];
  };
}
