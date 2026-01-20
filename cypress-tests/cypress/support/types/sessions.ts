export interface Environment {
  id?: string;
  name?: string;
  description?: string;
  container_image?: string;
  default_url?: string;
  mount_directory?: string;
  environment_kind?: string;
  environment_image_source?: string;
  uid?: number;
  gid?: number;
  working_directory?: string;
  port?: number;
  command?: string[];
  args?: string[];
}

export interface SessionLauncher {
  id?: string;
  name: string;
  project_id: string;
  environment: Environment;
  resource_class_id: number;
}

export interface Session {
  name: string;
  image: string;
  project_id: string;
  launcher_id: string;
  resource_class_id: string;
}

export interface SessionSecretSlot {
  id?: string;
  name: string;
  filename: string;
  description?: string;
  project_id: string;
}
