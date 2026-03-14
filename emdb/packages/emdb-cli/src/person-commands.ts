import { Command } from 'commander';
import { promises as fs } from 'fs';
import { z } from 'zod'; 

import { useEmdb } from './services/useEmdb.js';
import { useErrorHandler } from './composables/useErrorHandler.js';
import { SavePersonSchema } from './schemas/SavePersonSchema.js';

export { personCommand };

const { handleError } = useErrorHandler();

const personCommand = new Command('person')
  .description('Perform CRUD operations on a person');

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
