import axios from "axios";
const baseUrlSummary = 'http://localhost:8080/summary';

const getAllSummaries = async () => {
  const response = await axios.get(baseUrlSummary);
  return response.data;
};

const createNewSummary = async () => {
  const response = await axios.post(baseUrlSummary);
  return response.data;
}

export {
  getAllSummaries,
  createNewSummary
};
