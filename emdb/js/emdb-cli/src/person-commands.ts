import { Command } from 'commander';
import { promises as fs } from 'fs';

import { PersonService } from './service/PersonService.js';

export { personCommand };

const personCommand = new Command('person')
  .description('Perform CRUD operations on a person');

personCommand
  .command('create')
  .description('Create a person from TMDB in EMDB')
  .argument('<file>', 'The file containing the person to create')
  .addHelpText('after', `
Examples:
  $ emdb-cli person create Harrison-Ford.json
`)  
  .action(create);

personCommand
  .command('find')
  .description('Find a person in EMDB')
  .argument('<id>', 'The id of the person to find')
  .addHelpText('after', `
Examples:
  $ emdb-cli person find 1
`)  
  .action(findById);

personCommand
  .command('update')
  .description('Update a person in EMDB')
  .argument('<id>', 'The id of the person to update')
  .argument('<file>', 'The file containing the person update')
  .addHelpText('after', `
Examples:
  $ emdb-cli person update 1 Harrison-Ford-Update.json
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

async function create(fileName: string) {
  try {
    const personService = new PersonService();
    const file = await fs.readFile(fileName, 'utf-8');
    const start = performance.now();
    const person = await personService.create(JSON.parse(file));
    const et = (performance.now() - start).toLocaleString(undefined, {
      minimumFractionDigits: 1, maximumFractionDigits: 1
    });
    console.log(`Created: ${person.name} in: ${et} ms.`);
    console.log(person);
  } catch (error) {
    console.error(`Error creating person: ${error}`);
  }
}

async function findById(id: number) {
  try {
    const personService = new PersonService();
    const start = performance.now();
    const person = await personService.findById(id);
    const et = (performance.now() - start).toLocaleString(undefined, {
      minimumFractionDigits: 1, maximumFractionDigits: 1
    });
    console.log(`Found: ${person.name} in: ${et} ms.`);
    console.log(person);
  } catch (error) {
    console.error(`Error finding person: ${error}`);
  }  
}

async function update(id: number, fileName: string) {
  try {
    const personService = new PersonService();
    const file = await fs.readFile(fileName, 'utf-8');
    const start = performance.now();
    const person = await personService.update(id, JSON.parse(file));
       const et = (performance.now() - start).toLocaleString(undefined, {
      minimumFractionDigits: 1, maximumFractionDigits: 1
    });
    console.log(`Updated: ${person.name} in: ${et} ms.`);
    console.log(person);
  } catch (error) {
    console.error(`Error updating person: ${error}`);
  }
}

async function deleteById(id: number) {
  try {
    const personService = new PersonService();
    const start = performance.now();
    await personService.deleteById(id);
    const et = (performance.now() - start).toLocaleString(undefined, {
      minimumFractionDigits: 1, maximumFractionDigits: 1
    });
    console.log(`Deleted: ${id} in: ${et} ms.`);
  } catch (error) {
    console.error(`Error deleting person: ${error}`);
  }    
}