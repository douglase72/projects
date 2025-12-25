import axios, { type AxiosInstance } from 'axios';

import {
  type Person, 
  PersonCreateRequest, 
  PersonUpdateRequest, 
} from '@emdb/common';

export class PersonService {
  private readonly client: AxiosInstance;

  constructor() {
    this.client = axios.create({
      baseURL: process.env.BASE_URL,
    });
  }

  async create(request: PersonCreateRequest): Promise<Person> {
    const { data: person } = await this.client.post<Person>(`/people`, request);
    return person;
  }
  
  async findById(id: number): Promise<Person> {
    const { data: person } = await this.client.get<Person>(`/people/${id}?append=credits`);
    return person;    
  }

  async update(id: number, request: PersonUpdateRequest): Promise<Person> {
    const { data: person } = await this.client.patch<Person>(`/people/${id}`, request);
    return person;
  }
  
  async deleteById(id: number) {
    await this.client.delete<Person>(`/people/${id}`);
  }

}