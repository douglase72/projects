import type { CodegenConfig } from '@graphql-codegen/cli'

const config: CodegenConfig = {
  overwrite: true,
  schema: 'http://localhost:60310/emdb/graphql',
  documents: ['src/**/*.{ts,vue}', '!src/gql/**/*'], 
  ignoreNoDocuments: true,
  generates: {
    './src/gql/': { 
      preset: 'client',              
      presetConfig: {
        fragmentMasking: false,      
      },
      config: {
        useTypeImports: true,
        scalars: {                   
          BigInteger: 'number',
          Date: 'string',
          DateTime: 'string',
          BigDecimal: 'number',
        },
      },
    },
  },
}

export default config