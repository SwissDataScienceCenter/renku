export interface DataConnector {
  id?: string;
  name?: string;
  namespace: string;
  slug: string;
  description?: string;
  visibility?: "private" | "public";
}
