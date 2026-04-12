import { Command } from 'commander';
import { promises as fs } from 'fs';
import { z } from 'zod'; 

import { useEmdb } from './services/useEmdb.js';
import { useErrorHandler } from './services/useErrorHandler.js';
import { type IngestMedia, IngestSource, MediaType } from '@emdb/common';
import { SavePersonSchema } from './schemas/SavePersonSchema.js';
import { UpdatePersonSchema } from './schemas/UpdatePersonSchema.js';

export { personCommand };

const { handleError } = useErrorHandler();

const personCommand = new Command('person')
  .description('Perform CRUD operations on a person');

personCommand
  .command('ingest')
  .description('Ingest a person from TMDB')
  .argument('<tmdbId>', 'The TMDB id of the person to ingest')
  .addHelpText('after', `
Examples:
  $ emdb-cli person ingest 13918
`)  
  .action(ingest);

personCommand
  .command('save')
  .description('Save a person from TMDB')
  .argument('<file>', 'The file containing the person to save')
  .addHelpText('after', `
Examples:
  $ emdb-cli person save Harrison-Ford.json
`)  
  .action(save);

personCommand
  .command('batch')
  .description('Save people from TMDB')
  .argument('<file>', 'The file containing the people to save')
  .addHelpText('after', `
Examples:
  $ emdb-cli person batch peole.json
`)  
  .action(batch);

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
  .command('find-all')
  .description('Find all the people in EMDB')
  .addHelpText('after', `
Examples:
  $ emdb-cli people find-all
`)  
  .action(findAll);

personCommand
  .command('update')
  .description('Update a person from TMDB')
  .argument('<id>', 'The id of the person to update')
  .argument('<file>', 'The file containing the person update')
  .addHelpText('after', `
Examples:
  $ emdb-cli person update 1 Harrison-Ford.json
`)  
  .action(update);

personCommand
  .command('delete')
  .description('Delete a person in EMDB')
  .argument('<id>', 'The id of the person to delete')
  .addHelpText('after', `
Examples:
  $ emdb-cli person delete 1
`)  
  .action(deleteById);

async function ingest(tmdbId: number) {
  try {
    const { ingest } = useEmdb();
    const command: IngestMedia = {
      tmdbId: tmdbId,
      source: IngestSource.CLI,
      type: MediaType.PERSON
    };
    const jobId = await ingest(command);
    console.log(`Job Id: ${jobId}`);
  } catch (error: any) {
    handleError(error);
  }
};

async function save(fileName: string) {
  try {
    const file = await fs.readFile(fileName, 'utf-8');
    const parsed = SavePersonSchema.safeParse(JSON.parse(file));
    if (!parsed.success) {
      console.error(`Validation failed for file: ${fileName}`);
      console.error(JSON.stringify(parsed.error.format(), null, 2));
      process.exit(1);
    }
    const { savePerson } = useEmdb();
    const start = performance.now();
    const { status, person } = await savePerson(parsed.data);
    const et = (performance.now() - start).toLocaleString(undefined, {
      minimumFractionDigits: 1, maximumFractionDigits: 1
    });

    if (status === 201) {
      console.log(`Created person: ${person?.name} in ${et} ms.`);
      console.log(person);
    } else if (status === 200) {
      console.log(`Updated person: ${person?.name} in ${et} ms.`);
      console.log(person);
    } else if (status === 204) {
      console.log(`No changes needed for: ${parsed.data.name}. Skipped in ${et} ms.`);
    } else {
      console.log(`Saved with status ${status} in ${et} ms.`);
    } 
  } catch (error: any) {
    handleError(error);
  }
};

async function batch(fileName: string) {
  try {
    const file = await fs.readFile(fileName, 'utf-8');
    const parsed = z.array(SavePersonSchema).safeParse(JSON.parse(file));
    if (!parsed.success) {
      console.error(`Validation failed for file: ${fileName}`);
      console.error(JSON.stringify(parsed.error.format(), null, 2));
      process.exit(1);
    }
    const { savePeople } = useEmdb();
    const start = performance.now();
    const results = await savePeople(parsed.data);
    const et = (performance.now() - start).toLocaleString(undefined, {
      minimumFractionDigits: 1, maximumFractionDigits: 1
    });
    const created = results.filter(r => r.statusCode === 201).length;
    const updated = results.filter(r => r.statusCode === 200).length;
    const unchanged = results.filter(r => r.statusCode === 204).length;
    console.log(`Batch complete in: ${et} ms.`);
    console.log(`Created: ${created} | Updated: ${updated} | Unchanged: ${unchanged}`);
  } catch (error: any) {
    handleError(error);
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
  } catch (error: any) {
    handleError(error);
  }
};

async function findAll() {
  try {
    const { findAllPeople } = useEmdb();
    const start = performance.now();
    const people = await findAllPeople();
    const et = (performance.now() - start).toLocaleString(undefined, {
      minimumFractionDigits: 1, maximumFractionDigits: 1
    });
    console.log(`Found all people in: ${et} ms.`);    
    console.log(people);
  } catch (error: any) {
    handleError(error);
  }
};

async function update(id: number, fileName: string) {
  try {
    const file = await fs.readFile(fileName, 'utf-8');
    const parsed = UpdatePersonSchema.safeParse(JSON.parse(file));
    if (!parsed.success) {
      console.error(`Validation failed for file: ${fileName}`);
      console.error(JSON.stringify(parsed.error.format(), null, 2));
      process.exit(1);
    }
    const { updatePerson } = useEmdb();
    const start = performance.now();
    const { status, person } = await updatePerson(id, parsed.data);
    const et = (performance.now() - start).toLocaleString(undefined, {
      minimumFractionDigits: 1, maximumFractionDigits: 1
    });

    if (status === 201) {
      console.log(`Created movie: ${person?.name} in ${et} ms.`);
      console.log(person);
    } else if (status === 200) {
      console.log(`Updated movie: ${person?.name} in ${et} ms.`);
      console.log(person);
    } else if (status === 204) {
      console.log(`No changes needed for ${parsed.data.name}. Skipped in ${et} ms.`);
    } else {
      console.log(`Updated with status ${status} in ${et} ms.`);
    }
  } catch (error: any) {
    handleError(error);
  }
};

async function deleteById(id: number) {
  try {
    const { deletePerson } = useEmdb();
    const start = performance.now();
    await deletePerson(id);
    const et = (performance.now() - start).toLocaleString(undefined, {
      minimumFractionDigits: 1, maximumFractionDigits: 1
    });
    console.log(`Deleted person ${id} in: ${et} ms.`);    
  } catch (error: any) {
    handleError(error);
  }
};