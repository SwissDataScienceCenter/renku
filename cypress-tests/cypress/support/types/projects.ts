export type ProjectIdentifierV2 = {
  id?: string;
  namespace?: string;
  slug: string;
};

export interface ProjectV2 extends ProjectIdentifierV2 {
  description?: string;
  name: string;
  visibility?: "public" | "private";
}
