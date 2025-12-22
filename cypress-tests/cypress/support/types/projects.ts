export interface Project {
  id?: string;
  name: string;
  namespace: string;
  slug: string;
  description?: string;
  visibility?: "public" | "private";
}
