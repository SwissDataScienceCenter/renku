export type GroupIdentifier = {
  id?: string;
  slug: string;
};

export interface NewGroupBody extends GroupIdentifier {
  description?: string;
  name: string;
}
