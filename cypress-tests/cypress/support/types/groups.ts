export type GroupIdentifier = {
  id?: string;
  slug: string;
};

export interface Group extends GroupIdentifier {
  description?: string;
  name: string;
}
