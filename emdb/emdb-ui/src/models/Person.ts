
export enum Gender {
  UNKNOWN = "Unknown",
  FEMALE = "Female",
  MALE = "Male",
  NON_BINARY = "Non-Binary"
}

export function fromGender(status: string): string {
  return Gender[status as keyof typeof Gender] ?? status;
}