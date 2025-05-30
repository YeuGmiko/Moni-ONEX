import type {
  AxiosInstance,
  AxiosRequestConfig,
  AxiosResponse,
  InternalAxiosRequestConfig
} from 'axios'
import { AxiosError } from 'axios'

export interface Response<T = unknown> {
  code: number,
  msg: string,
  data: T
}

export interface FlatResponseSuccessData<T = any, ResponseData = any> {
  data: T
  error: null
  response: AxiosResponse<ResponseData>
}

export interface FlatResponseFailData<ResponseData = any> {
  data: null
  error: AxiosError<ResponseData>
  response: AxiosResponse<ResponseData>
}

export type FlatResponseData<T = any, ResponseData = any> =
    | FlatResponseSuccessData<T, ResponseData>
    | FlatResponseFailData<ResponseData>

export interface FlatRequestInstance<ResponseData = any> {
  <T = any>(config: AxiosRequestConfig): Promise<FlatResponseData<T, ResponseData>>
}
export interface RequestInstanceOptions<ResponseData = any> {
  onRequest?: (
      config: InternalAxiosRequestConfig
  ) => InternalAxiosRequestConfig | Promise<InternalAxiosRequestConfig>
  isBackendSuccess?: (response: AxiosResponse<ResponseData>) => Promise<boolean> | boolean
  transformResponse?: (
      response: AxiosResponse<ResponseData>
  ) => Promise<any> | any
  onBackendFail?: (
      response: AxiosResponse<ResponseData>,
      instance: AxiosInstance
  ) => Promise<AxiosResponse | null> | Promise<void>
  onError?: (error: AxiosError<ResponseData>) => void | Promise<void>
}
