export interface DataConnector {
  description?: string;
  name?: string;
  namespace: string;
  slug: string;
  visibility?: "private" | "public";
}
