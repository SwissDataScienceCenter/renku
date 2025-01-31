export type ProjectIdentifierV2 = {
  id?: string;
  namespace?: string;
  slug: string;
};

export interface NewProjectV2Body extends ProjectIdentifierV2 {
  name: string;
  visibility?: "public" | "private";
}
