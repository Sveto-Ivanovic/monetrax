// utils/getErrorMessage.ts
import { FetchError } from "ofetch";
import type { CommonErrorResponse } from "~/services/usermanagement/types";

export function getErrorMessage(error: unknown): string {
  if (error instanceof FetchError) {
    if (error.data && "errors" in error.data) {
      const res = error.data as CommonErrorResponse;
      return res.errors.map(err => err.message).join(", ");
    }
    return "An unexpected error occurred.";
  }
  if (error instanceof Error) return error.message;
  return "An unexpected error occurred.";
}