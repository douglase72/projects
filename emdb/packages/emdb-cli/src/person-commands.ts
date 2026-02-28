import { Command } from 'commander';
import { promises as fs } from 'fs';

import { useEmdb } from './services/useEmdb.js';
import { IngestSource, MediaType } from '@emdb/common';
import { PersonSchema } from './schemas/PersonSchema.js';

export { personCommand };

const personCommand = new Command('person')
  .description('Perform CRUD operations on a series');

personCommand
  .command('delete')
  .description('Delete a person in EMDB')
  .argument('<id>', 'The id of the person to delete')
  .addHelpText('after', `
Examples:
  $ emdb-cli person delete 1
`)  
  .action(deleteById);

personCommand
  .command('find')
  .description('Find a person in EMDB')
  .argument('<id>', 'The id of the person to find')
  .addHelpText('after', `
Examples:
  $ emdb-cli person find 1
`)  
  .action(find);

personCommand
  .command('ingest')
  .description('Ingest a person from TMDB into EMDB')
  .argument('<tmdbId>', 'The TMDB id of the person to ingest')
  .addHelpText('after', `
Examples:
  $ emdb-cli person ingest 3
`)  
  .action(ingest);

personCommand
  .command('save')
  .description('Save a person from TMDB')
  .argument('<file>', 'The file containing the person to save')
  .addHelpText('after', `
Examples:
  $ emdb-cli person save Harrison-Ford-20260205-202941.json
`)  
  .action(save);

personCommand
  .command('update')
  .description('Update a person from TMDB')
  .argument('<id>', 'The id of the person to update')
  .argument('<file>', 'The file containing the person to update')
  .addHelpText('after', `
Examples:
  $ emdb-cli person update Harrison-Ford-20260205-202941.json
`)  
  .action(update);

async function deleteById(id: number) {
  try {
    const { deletePerson } = useEmdb();
    const start = performance.now();
    await deletePerson(id);
    const et = (performance.now() - start).toLocaleString(undefined, {
      minimumFractionDigits: 1, maximumFractionDigits: 1
    });
    console.log(`Deleted ${id} in: ${et} ms.`);  
  }  catch (error: any) {
    console.dir(error, { depth: null }); 
    process.exit(1);
  }
};  

async function find(id: number) {
  try {
    const { findPerson } = useEmdb();
    const start = performance.now();
    const person = await findPerson(id);
    const et = (performance.now() - start).toLocaleString(undefined, {
      minimumFractionDigits: 1, maximumFractionDigits: 1
    });
    console.log(`Found ${person.name} in: ${et} ms.`);     
    console.log(person);
  }  catch (error: any) {
    console.dir(error, { depth: null }); 
    process.exit(1);
  }
};

async function ingest(tmdbId: number) {
  try {
    const { ingest } = useEmdb();
    const command = {
      tmdbId: tmdbId,
      source: IngestSource.CLI,
      type: MediaType.PERSON
    };
    const jobId = await ingest(command);
    console.log(`Job Id: ${jobId}`);
  }  catch (error: any) {
    console.dir(error, { depth: null }); 
    process.exit(1);
  }
};

async function save(fileName: string) {
  try {
    const file = await fs.readFile(fileName, 'utf-8');
    const parsed = PersonSchema.safeParse(JSON.parse(file));
    if (!parsed.success) {
      console.error(`Validation failed for file: ${fileName}`);
      console.error(JSON.stringify(parsed.error.format(), null, 2));
      process.exit(1);
    }
    const { savePerson } = useEmdb();
    const start = performance.now();
    const person = await savePerson(parsed.data);
    const et = (performance.now() - start).toLocaleString(undefined, {
      minimumFractionDigits: 1, maximumFractionDigits: 1
    });
    console.log(`Saved ${person.name} in: ${et} ms.`);     
    console.log(person);
  } catch (error: any) {
    console.dir(error, { depth: null }); 
    process.exit(1);
  }
};

async function update(id:number, fileName: string) {
  try {
    const file = await fs.readFile(fileName, 'utf-8');
    const { updatePerson } = useEmdb();
    const start = performance.now();
    const person = await updatePerson(id, JSON.parse(file));
    const et = (performance.now() - start).toLocaleString(undefined, {
      minimumFractionDigits: 1, maximumFractionDigits: 1
    });    
    console.log(`Updated ${person.name} in: ${et} ms.`);
    console.log(person);
  } catch (error: any) {
    console.dir(error, { depth: null }); 
    process.exit(1);
  }
};