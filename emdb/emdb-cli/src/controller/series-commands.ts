import { Command } from 'commander';

const seriesCommand = new Command('series')
  .description('Perform CRUD operations on TV series');

seriesCommand
  .command('ingest')
  .description('Ingest a TV series from TMDB into EMDB')
  .argument('<id>', 'The TMDB id of the TV series to ingest')
  .action(ingest);
  
async function ingest(id: number) {
  try {
    const start = performance.now();
    console.log(`Sending ingest request ...`)
    const et = (performance.now() - start).toLocaleString(undefined, {
      minimumFractionDigits: 1,
      maximumFractionDigits: 1
    });
    console.log(`Ingest request for TMDB movie: ${id} took: ${et} ms.`);
  } catch (error) {
    console.error(`Error sending ingest request: ${error}`);
  }   
}

export { seriesCommand };