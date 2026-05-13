export type ProjectVisibility = "public" | "private";

export interface ProjectPost {
  name: string;
  namespace: string;
  slug: string;
  description?: string;
  visibility?: ProjectVisibility;
}

export interface Project {
  id: string;
  name: string;
  namespace: string;
  slug: string;
}
