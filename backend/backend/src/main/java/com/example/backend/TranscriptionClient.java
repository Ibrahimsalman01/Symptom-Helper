package com.example.backend;

import com.google.api.gax.rpc.ClientStream;
import com.google.api.gax.rpc.ResponseObserver;
import com.google.api.gax.rpc.StreamController;
import com.google.cloud.speech.v1.*;
import com.google.protobuf.ByteString;

import javax.sound.sampled.*;
import java.util.ArrayList;

public class TranscriptionClient {

    /** This field stores the transcription as an ArrayList of Strings. */
    private ArrayList<String> transcription;

    public TranscriptionClient() {
        this.transcription = new ArrayList<>();
    }

    /** This method records up to 1 minute of microphone activity and transcribes the resulting input */
    private void streamingMicRecognize() throws Exception {

        ResponseObserver<StreamingRecognizeResponse> responseObserver = null;
        try (SpeechClient client = SpeechClient.create()) {

            // this response observer holds the resulting transcribed speech
            // in an arraylist
            responseObserver =
                    new ResponseObserver<StreamingRecognizeResponse>() {
                        ArrayList<StreamingRecognizeResponse> responses = new ArrayList<>();

                        public void onStart(StreamController controller) {}

                        public void onResponse(StreamingRecognizeResponse response) {
                            responses.add(response);
                        }

                        // after the program is done recording input it should return an arraylist of the transcription
                        public void onComplete() {
                            if (responses.isEmpty()) return;

                            for (StreamingRecognizeResponse response : responses) {
                                StreamingRecognitionResult result = response.getResultsList().get(0);
                                SpeechRecognitionAlternative alt = result.getAlternativesList().get(0);

                                String s = alt.getTranscript().trim();
                                String sFormatted = s.substring(0, 1).toUpperCase()
                                        + s.substring(1) + ". ";

                                // Add transcription to the instance's transcription field
                                appendTranscriptionLine(sFormatted);
                            }
                        }

                        public void onError(Throwable t) {
                            System.out.println(t);
                        }
                    };

            ClientStream<StreamingRecognizeRequest> clientStream =
                    client.streamingRecognizeCallable().splitCall(responseObserver);

            /** Tells the recognizer how to process the transcription request:
             * Encoding: Format of the audio data sent for transcription
             *           (LINEAR16 and FLAC are best)
             * LanguageCode: The language the transcript will be generated in
             *               (currently using en-US, can also use en-CA)
             * SampleRateHertz: Audio samples per second (similar to FPS). Higher
             *                  sample rates equates to higher quality sound. 16 kHz
             *                  is fine due to human speech being up to 8 kHz at a maximum
             *                  (Nyquist-Shannon theorem, sample rate should be 2x the
             *                  highest frequency in an audio wave to capture properly)
             */
            RecognitionConfig recognitionConfig =
                    RecognitionConfig.newBuilder()
                            .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                            .setLanguageCode("en-US")
                            .setSampleRateHertz(16000)
                            .build();
            StreamingRecognitionConfig streamingRecognitionConfig =
                    StreamingRecognitionConfig.newBuilder().setConfig(recognitionConfig).build();

            StreamingRecognizeRequest request =
                    StreamingRecognizeRequest.newBuilder()
                            .setStreamingConfig(streamingRecognitionConfig)
                            .build(); // The first request in a streaming call has to be a config

            clientStream.send(request);

            /** Formatting for the audio recorded as input */
            AudioFormat audioFormat = new AudioFormat(
                    16000,
                    16,
                    1,
                    true,
                    false
            );
            DataLine.Info targetInfo =
                    new DataLine.Info(
                            TargetDataLine.class,
                            audioFormat); // Set the system information to read from the microphone audio stream

            // If no mic is detected, the program won't work
            if (!AudioSystem.isLineSupported(targetInfo)) {
                System.out.println("Microphone not supported");
                System.exit(0);
            }

            // Target data line captures the audio stream the microphone produces.
            TargetDataLine targetDataLine = (TargetDataLine) AudioSystem.getLine(targetInfo);
            targetDataLine.open(audioFormat);
            targetDataLine.start();
            System.out.println("Start speaking!");
            long startTime = System.currentTimeMillis();

            // Audio Input Stream
            AudioInputStream audio = new AudioInputStream(targetDataLine);
            while (true) {
                long estimatedTime = System.currentTimeMillis() - startTime;
                byte[] data = new byte[6400];
                audio.read(data);

                if (estimatedTime > 15000) { // 15 seconds
                    System.out.println("Stop speaking.");
                    targetDataLine.stop();
                    targetDataLine.close();
                    break;
                }
                request =
                        StreamingRecognizeRequest.newBuilder()
                                .setAudioContent(ByteString.copyFrom(data))
                                .build();

                clientStream.send(request);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        responseObserver.onComplete();
    }

    /** Adds a transcription line to the transcription ArrayList */
    private void appendTranscriptionLine(String line) {
        this.transcription.add(line);
    }

    /** Returns the transcription ArrayList */
    private ArrayList<String> getTranscription() {
        return this.transcription;
    }

    public String executeTranscription() throws Exception {
        streamingMicRecognize();
        return transcriptionToString(getTranscription());
    }

    public String transcriptionToString(ArrayList<String> transcript) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String line : transcript) stringBuilder.append(line);
        return stringBuilder.toString().trim();
    }

    public static void main(String[] args) throws Exception {
        TranscriptionClient transcriptionClient = new TranscriptionClient();
        String stringTranscript = transcriptionClient.executeTranscription();
        System.out.println(stringTranscript);

    }
}
