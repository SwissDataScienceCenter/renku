export interface Environment {
  id?: string;
  name?: string;
  description?: string;
  container_image?: string;
  default_url?: string;
  uid?: number;
  gid?: number;
  working_directory?: string;
  mount_directory?: string;
  port?: number;
  environment_kind?: string;
  environment_image_source?: string;
}

export interface SessionLauncher {
  id?: string;
  name: string;
  project_id: string;
  environment: Environment;
  resource_class_id: number;
}
