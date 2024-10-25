import { useCallback, useEffect, useState } from 'react';
import { getAllSummaries, createNewSummary } from './services/summary';
import "./App.css";

const App = () => {
  const [ summaries, setSummaries ] = useState([]);

  const summaryRetrieval = useCallback(async () => {
    try {
      const retrievedSummaries = await getAllSummaries();
      setSummaries((prevDescs) => prevDescs.concat(retrievedSummaries))
    } catch (e) {
      console.log(`Error retrieving summaries from the DB: ${e}`);
    }
  }, []);


  const addSummary = async () => {
    try {
      const summary = await createNewSummary();
      setSummaries((prevSummaries) => [...prevSummaries, summary]);
    } catch (e) {
      console.log(e);
    }
  }

  useEffect(() => {
    summaryRetrieval();
  }, [summaryRetrieval]);

  return (
    <div className='div-main-page'>
      <div className='div-title'>
        <h1>Symptom Helper</h1>
      </div>
      <div className='div-record'>
        <button className='button-record' onClick={() => addSummary()}>
          Record
        </button>
      </div>
      <div>
        <ul className='ul-symptom-list'>
          {
            summaries.map(
              (obj) => 
                <li key={obj.summaryId}>
                  <p className='p-summary-date'>
                    {obj.summary} 
                    <p className='p-date'>{new Date(obj.date).toLocaleString()}</p>
                  </p>
                </li>
            )
          }
        </ul>
      </div>
    </div>
  );
};

export default App;
