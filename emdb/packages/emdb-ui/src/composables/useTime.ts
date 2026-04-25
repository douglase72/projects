
export function useTime() {
  
  const toDate = (dateString: string | null | undefined) => {
    if (!dateString) return null;
    const [year, month, day] = dateString.split('-').map(Number) as [number, number, number];
    return new Date(year, month - 1, day); 
  };

  const toDateString = (date: Date | null) => {
    return date ? date.toLocaleDateString('en-CA') : null;
  };

  const toDateTime = (time: string) => {
    const d = new Date(time);
    const pad = (n: number) => n.toString().padStart(2, '0');
    const pad3 = (n: number) => n.toString().padStart(3, '0');
    return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}.${pad3(d.getMilliseconds())}`;
  };  

  return {
    toDate,
    toDateString,
    toDateTime,
  }
}