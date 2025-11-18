import { Command } from 'commander';
import { promises as fs } from 'fs';

import { SeriesService } from '../service/SeriesService.js';

const seriesCommand = new Command('series')
  .description('Perform CRUD operations on TV series');

seriesCommand
  .command('ingest')
  .description('Ingest a TV series from TMDB into EMDB')
  .argument('<id>', 'The TMDB id of the TV series to ingest')
  .action(ingest);

seriesCommand
  .command('find')
  .description('Find a series in EMDB')
  .argument('<id>', 'The id of the series to find')
  .addHelpText('after', `
Examples:
  $ emdb-cli series find 1
`)  
  .action(findById);

seriesCommand
  .command('delete')
  .description('Delete a series in EMDB')
  .argument('<id>', 'The id of the series to delete')
  .addHelpText('after', `
Examples:
  $ emdb-cli series delete 1
`)  
  .action(deleteById);

seriesCommand
  .command('update')
  .description('Update a series in EMDB')
  .argument('<id>', 'The id of the series to update')
  .argument('<file>', 'The file containing the series update')
  .addHelpText('after', `
Examples:
  $ emdb-cli series update 1 /home/erdouglass/projects/emdb/emdb-cli/series/the-simpsons.json
`)
  .action(update);
  
async function ingest(id: number) {
  try {
    const seriesService = new SeriesService();
    const start = performance.now();
    await seriesService.ingest({ tmdbId: id });
    const et = (performance.now() - start).toLocaleString(undefined, {
      minimumFractionDigits: 1,
      maximumFractionDigits: 1
    });
    console.log(`Ingest request for TMDB series: ${id} took: ${et} ms.`);
  } catch (error) {
    console.error(`Error sending series ingest request: ${error}`);
  }   
}

async function findById(id: number) {
  try {
    const seriesService = new SeriesService();
    const start = performance.now();
    const series = await seriesService.findById(id);
    const et = (performance.now() - start).toLocaleString(undefined, {
      minimumFractionDigits: 1,
      maximumFractionDigits: 1
    });
    console.log(`Found ${series.name} in: ${et} ms.`);
    console.log(series);
  } catch (error) {
    console.error(`Error finding EMDB series: ${error}`);
  }  
}

async function update(id: number, fileName: string) {
  try {
    const seriesService = new SeriesService();
    const seriesFile = await fs.readFile(fileName, 'utf-8');
    const start = performance.now();
    const series = await seriesService.update(id, JSON.parse(seriesFile));
       const et = (performance.now() - start).toLocaleString(undefined, {
      minimumFractionDigits: 1,
      maximumFractionDigits: 1
    });
    console.log(`Updated ${series.name} in: ${et} ms.`);
    console.log(series);
  } catch (error) {
    console.error(`Error updating EMDB series: ${error}`);
  }
}

async function deleteById(id: number) {
  try {
    const seriesService = new SeriesService();
    const start = performance.now();
    await seriesService.deleteById(id);
    const et = (performance.now() - start).toLocaleString(undefined, {
      minimumFractionDigits: 1,
      maximumFractionDigits: 1
    });
    console.log(`Deleted series ${id} in: ${et} ms.`);
  } catch (error) {
    console.error(`Error deleting EMDB series: ${error}`);
  }    
}

export { seriesCommand };