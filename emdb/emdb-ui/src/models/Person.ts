
export enum Gender {
  UNKNOWN = "Unknown",
  FEMALE = "Female",
  MALE = "Male",
  NON_BINARY = "Non-Binary"
}

export function fromGender(gender: string): string {
  return Gender[gender as keyof typeof Gender] ?? gender;
}