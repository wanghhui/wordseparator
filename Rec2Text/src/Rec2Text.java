
//sox test.wav -b 16 out.wav rate 8000
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

import com.iflytek.cloud.speech.LexiconListener;
import com.iflytek.cloud.speech.RecognizerListener;
import com.iflytek.cloud.speech.RecognizerResult;
import com.iflytek.cloud.speech.Setting;
import com.iflytek.cloud.speech.SpeechConstant;
import com.iflytek.cloud.speech.SpeechError;
import com.iflytek.cloud.speech.SpeechEvent;
import com.iflytek.cloud.speech.SpeechRecognizer;
import com.iflytek.cloud.speech.SpeechSynthesizer;
import com.iflytek.cloud.speech.SpeechUtility;
import com.iflytek.cloud.speech.SynthesizeToUriListener;
import com.iflytek.cloud.speech.UserWords;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

public class Rec2Text {

    private static final String APPID = "58d0c23d";

    //private static final String USER_WORDS = "{\"userword\":[{\"name\":\"计算机词汇\",\"words\":[\"随机存储器\",\"只读存储器\",\"扩充数据输出\",\"局部总线\",\"压缩光盘\",\"十七寸显示器\"]},{\"name\":\"我的词汇\",\"words\":[\"槐花树老街\",\"王小贰\",\"发炎\",\"公事\"]}]}";

    private static Rec2Text mObject;

    private static StringBuffer mResult = new StringBuffer();

    private boolean mIsLoop = true;

    private String filename="";
    private double voiceLenth = 0;


    public static void main(String args[]) {
        if (null != args && args.length > 0 && args[0].equals("true")) {
            //在应用发布版本中，请勿显示日志，详情见此函数说明。
            Setting.setShowLog(true);
        }


        SpeechUtility.createUtility("appid=" + APPID);

        getMscObj().loop();
    }

    private static Rec2Text getMscObj() {
        if (mObject == null)
            mObject = new Rec2Text();
        return mObject;
    }

    private boolean onLoop() {
        boolean isWait = true;
        try {

            File folder = new File("./queue");
            File[] listOfFiles = folder.listFiles();

            for (int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isFile()) {
                    System.out.println("File " + listOfFiles[i].getAbsolutePath());
                    Recognize(listOfFiles[i].getAbsolutePath());
                    listOfFiles[i].delete();
                    Thread.sleep(10000);



                } else if (listOfFiles[i].isDirectory()) {
                    System.out.println("Directory " + listOfFiles[i].getName());
                }
            }

//            DebugLog.Log("*********************************");
//            DebugLog.Log("Please input the command");
//            DebugLog.Log("1:音频流听写            2：上传词表           3：无声合成           4：退出  ");
//
//            Scanner in = new Scanner(System.in);
//            int command = in.nextInt();
//
//            DebugLog.Log("You input " + command);
//
//            switch (command) {
//                case 1:
//                    Recognize();
//                    break;
//                case 2:
//                    uploadUserWords();
//                    break;
//                case 3:
//                    Synthesize();
//                    break;
//                case 4:
//                    mIsLoop = false;
//                    isWait = false;
//                    in.close();
//                    break;
//                default:
//                    isWait = false;
//                    break;
//            }
        } catch (Exception e) {

        }

        return isWait;
    }

    // *************************************音频流听写*************************************

    /**
     * 听写
     */

    private boolean mIsEndOfSpeech = false;

    private void Recognize(String filename) {
        if (SpeechRecognizer.getRecognizer() == null)
            SpeechRecognizer.createRecognizer();
        mIsEndOfSpeech = false;
        try {
            RecognizePcmfileByte(filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 自动化测试注意要点 如果直接从音频文件识别，需要模拟真实的音速，防止音频队列的堵塞
     */
    public void RecognizePcmfileByte(String fullPath_filename) throws IOException {

//			File file = new File("./output.wav");
//			if(!file.exists()){
//				throw new RuntimeException("要读取的文件不存在");
//			}
//			FileInputStream fis = new FileInputStream(file);
//			int len = 0;
//			byte[] buf = new byte[fis.available()];
//			fis.read(buf);
//			fis.close();
//
////1.创建SpeechRecognizer对象
//		SpeechRecognizer mIat= SpeechRecognizer.createRecognizer( );
////2.设置听写参数，详见《MSC Reference Manual》SpeechConstant类
//		mIat.setParameter(SpeechConstant.DOMAIN, "iat");
//		mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
//		mIat.setParameter (SpeechConstant.ACCENT, "mandarin ");
//		mIat.setParameter(SpeechConstant.AUDIO_SOURCE, "-1");
////3.开始听写
//		mIat.startListening(recListener);
//
////voiceBuffer为音频数据流，splitBuffer为自定义分割接口，将其以4.8k字节分割成数组
//		ArrayList<byte[]> buffers = splitBuffer(buf,buf.length, 4800);
//		for (int i = 0; i < buffers.size(); i++) {
//			// 每次写入msc数据4.8K,相当150ms录音数据
//			mIat.writeAudio(buffers.get(i), 0, buffers.get(i).length);
//		}
//		mIat.stopListening();


        SpeechRecognizer recognizer = SpeechRecognizer.createRecognizer();
        recognizer.setParameter(SpeechConstant.AUDIO_SOURCE, "-1");
        //写音频流时，文件是应用层已有的，不必再保存
//		recognizer.setParameter(SpeechConstant.ASR_AUDIO_PATH,
//				"./iat_test.pcm");
        recognizer.setParameter(SpeechConstant.RESULT_TYPE, "plain");
        recognizer.setParameter(SpeechConstant.SAMPLE_RATE,"8000");
        recognizer.setParameter(SpeechConstant.VAD_EOS,"8000");

        recognizer.startListening(recListener);

        FileInputStream fis = null;
        File file = null;
        final byte[] buffer = new byte[64 * 1024];
        try {
            file = new File(fullPath_filename);
            fis = new FileInputStream(file);
            filename = fullPath_filename.substring(fullPath_filename.lastIndexOf('\\')+1);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
            AudioFormat format = audioInputStream.getFormat();
            long frames = audioInputStream.getFrameLength();
            voiceLenth = (frames+0.0) / format.getFrameRate();
            audioInputStream.close();

            if (0 == fis.available()) {
                mResult.append("no audio avaible!");
                recognizer.cancel();
            } else {
                byte[] buf = new byte[fis.available()];
                fis.read(buf);
                fis.close();

                ArrayList<byte[]> buffers = splitBuffer(buf, buf.length, 4800);
                for (int i = 0; i < buffers.size(); i++) {
                    // 每次写入msc数据4.8K,相当150ms录音数据
                    recognizer.writeAudio(buffers.get(i), 0, buffers.get(i).length);
                }

//				int lenRead = buffer.length;
//				while( buffer.length==lenRead && !mIsEndOfSpeech ){
//					lenRead = fis.read( buffer );
//					recognizer.writeAudio( buffer, 0, lenRead );
//				}//end of while

                recognizer.stopListening();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                file.delete();
                if (null != fis) {
                    fis.close();
                    fis = null;

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }//end of try-catch-finally

    }

    private ArrayList<byte[]> splitBuffer(byte[] buffer, int length, int spsize) {
        ArrayList<byte[]> array = new ArrayList<byte[]>();
        if (spsize <= 0 || length <= 0 || buffer == null
                || buffer.length < length)
            return array;
        int size = 0;
        while (size < length) {
            int left = length - size;
            if (spsize < left) {
                byte[] sdata = new byte[spsize];
                System.arraycopy(buffer, size, sdata, 0, spsize);
                array.add(sdata);
                size += spsize;
            } else {
                byte[] sdata = new byte[left];
                System.arraycopy(buffer, size, sdata, 0, left);
                array.add(sdata);
                size += left;
            }
        }
        return array;
    }

    /**
     * 听写监听器
     */
    private RecognizerListener recListener = new RecognizerListener() {

        public void onBeginOfSpeech() {
            DebugLog.Log("onBeginOfSpeech enter");
            DebugLog.Log("*************开始录音*************");
        }

        public void onEndOfSpeech() {
            DebugLog.Log("onEndOfSpeech enter");
            mIsEndOfSpeech = true;
        }

        public void onVolumeChanged(int volume) {
         //   DebugLog.Log("onVolumeChanged enter");
           // if (volume > 0)                DebugLog.Log("*************音量值:" + volume + "*************");

        }

        public void onResult(RecognizerResult result, boolean islast) {
            DebugLog.Log("onResult enter");
            mResult.append(result.getResultString());

            if (islast) {
                DebugLog.Log("识别结果为:" + mResult.toString());
                String url = "jdbc:sqlite:../taxfts.db";
                try {
                    Connection conn = DriverManager.getConnection(url);
                    System.out.println("Connection to SQLite has been established.");
                    String sql = "INSERT into voice_text (file_name,voice_lenth_in_second,V2Text) VALUES(?,?,?)";
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1,filename);
                    pstmt.setDouble(2,voiceLenth);
                    pstmt.setString(3,mResult.toString());
                    pstmt.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                mIsEndOfSpeech = true;
                mResult.delete(0, mResult.length());
                waitupLoop();
            }
        }

        public void onError(SpeechError error) {
            mIsEndOfSpeech = true;
            DebugLog.Log("*************" + error.getErrorCode()
                    + "*************");
            waitupLoop();
        }

        public void onEvent(int eventType, int arg1, int agr2, String msg) {
            DebugLog.Log("onEvent enter");
        }

    };

    // *************************************无声合成*************************************

    /**
     * 合成
     */
    private void Synthesize() {
        SpeechSynthesizer speechSynthesizer = SpeechSynthesizer
                .createSynthesizer();
        // 设置发音人
        speechSynthesizer.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");

        //启用合成音频流事件，不需要时，不用设置此参数
        speechSynthesizer.setParameter(SpeechConstant.TTS_BUFFER_EVENT, "1");
        // 设置合成音频保存位置（可自定义保存位置），默认不保存
        speechSynthesizer.synthesizeToUri("语音合成测试程序 ", "./tts_test.pcm",
                synthesizeToUriListener);
    }

    /**
     * 合成监听器
     */
    SynthesizeToUriListener synthesizeToUriListener = new SynthesizeToUriListener() {

        public void onBufferProgress(int progress) {
            DebugLog.Log("*************合成进度*************" + progress);

        }

        public void onSynthesizeCompleted(String uri, SpeechError error) {
            if (error == null) {
                DebugLog.Log("*************合成成功*************");
                DebugLog.Log("合成音频生成路径：" + uri);
            } else
                DebugLog.Log("*************" + error.getErrorCode()
                        + "*************");
            waitupLoop();

        }


        @Override
        public void onEvent(int eventType, int arg1, int arg2, int arg3, Object obj1, Object obj2) {
            if (SpeechEvent.EVENT_TTS_BUFFER == eventType) {
                DebugLog.Log("onEvent: type=" + eventType
                        + ", arg1=" + arg1
                        + ", arg2=" + arg2
                        + ", arg3=" + arg3
                        + ", obj2=" + (String) obj2);
                ArrayList<?> bufs = null;
                if (obj1 instanceof ArrayList<?>) {
                    bufs = (ArrayList<?>) obj1;
                } else {
                    DebugLog.Log("onEvent error obj1 is not ArrayList !");
                }//end of if-else instance of ArrayList

                if (null != bufs) {
                    for (final Object obj : bufs) {
                        if (obj instanceof byte[]) {
                            final byte[] buf = (byte[]) obj;
                            DebugLog.Log("onEvent buf length: " + buf.length);
                        } else {
                            DebugLog.Log("onEvent error element is not byte[] !");
                        }
                    }//end of for
                }//end of if bufs not null
            }//end of if tts buffer event
        }

    };

    // *************************************词表上传*************************************

    /**
     * 词表上传
     */
    private void uploadUserWords() {
        SpeechRecognizer recognizer = SpeechRecognizer.getRecognizer();
        if (recognizer == null) {
            recognizer = SpeechRecognizer.createRecognizer();

            if (null == recognizer) {
                DebugLog.Log("获取识别实例实败！");
                waitupLoop();
                return;
            }
        }

       // UserWords userwords = new UserWords(USER_WORDS);
        recognizer.setParameter(SpeechConstant.DATA_TYPE, "userword");
//        recognizer.updateLexicon("userwords",
//                userwords.toString(),
//                lexiconListener);
    }

    /**
     * 词表上传监听器
     */
    LexiconListener lexiconListener = new LexiconListener() {
        @Override
        public void onLexiconUpdated(String lexiconId, SpeechError error) {
            if (error == null)
                DebugLog.Log("*************上传成功*************");
            else
                DebugLog.Log("*************" + error.getErrorCode()
                        + "*************");
            waitupLoop();
        }

    };

    private void waitupLoop() {
        synchronized (this) {
            Rec2Text.this.notify();
        }
    }

    public void loop() {
        while (mIsLoop) {
            try {
                if (onLoop()) {
                    synchronized (this) {
                        this.wait();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
