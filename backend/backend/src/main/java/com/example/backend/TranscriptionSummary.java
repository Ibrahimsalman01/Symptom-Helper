package com.example.backend;

import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.ResponseHandler;

public class TranscriptionSummary {
    private final String projectId;
    private final String location;
    private final String modelName;
    private static final String prompt =
            "If the provided transcription isn't relevant to a biological symptom respond with: \"Please try again\". " +
                    "Summarize the symptom transcription in third person, focusing on the individual " +
                    "and the symptom. Avoid mentioning severity or location unless the individual " +
                    "explicitly states them in the transcription. Keep the summary concise and avoid " +
                    "using emotional language and the word \"Patient\" in a medical context. Begin: ";

    public TranscriptionSummary() {
        this.projectId = "symptom-helper-stt";
        this.location = "us-central1";
        this.modelName = "gemini-1.5-flash-001";
    }

    public String promptString(String transcription) throws Exception {
        if (transcription.isEmpty()) {
            throw new Exception("Transcription is empty");
        }
        return prompt + transcription;
    }

    public String textInput(String transcription) throws Exception {
        String transcriptionPrompt = promptString(transcription);

        try (VertexAI vertexAI = new VertexAI(projectId, location)) {
            GenerativeModel model = new GenerativeModel(modelName, vertexAI);
            GenerateContentResponse response = model.generateContent(transcriptionPrompt);
            return ResponseHandler
                    .getText(response)
                    .replace(" \n", "")
                    .replace("\\", "");
        }
    }

    public static void main(String[] args) throws Exception {
        TranscriptionSummary ts = new TranscriptionSummary();
        String result = ts.textInput("hello world");
        System.out.println(result);
    }
}
