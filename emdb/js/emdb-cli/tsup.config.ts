import { defineConfig } from 'tsup';

export default defineConfig({
  entry: ['src/emdb-cli.ts'],
  format: ['esm'],
  clean: true,
  // This explicitly tells tsup to bundle your common library
  // instead of treating it as an external require().
  noExternal: ['@emdb/common'], 
});