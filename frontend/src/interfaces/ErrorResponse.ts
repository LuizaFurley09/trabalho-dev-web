export interface ErrorResponse {
  localDateTime: Date;
  errorCode: number;
  error: string;
  metodo: string;
  requestUri: string;
  map: {
    [key: string]: string;
  };
  message: string;
}
