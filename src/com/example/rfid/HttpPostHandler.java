package com.example.rfid;

import android.os.Handler;
import android.os.Message;

/**
 * HTTP�ʐM��POST�^�X�N�������ɁC�ʐM�̐��ۂɉ����āC��M�����ʐM���e��UI��Ŏ�舵�����߂̒��ۃN���X�B
 *
 */
public abstract class HttpPostHandler extends Handler {

  // ���̃��\�b�h�͉B�؂����CMessage�Ȃǂ̒჌�x���I�u�W�F�N�g��
  // ���ڈ���Ȃ��ł��悢�悤�ɂ�����
  public void handleMessage(Message msg)
  {
    boolean isPostSuccess = msg.getData().getBoolean("http_post_success");
    String http_response = msg.getData().get("http_response").toString();

    if( isPostSuccess )
    {
      onPostCompleted( http_response );
    }
    else
    {
      onPostFailed( http_response );
    }
  }


  // ���L��override�������ɒ��ۉ��������R�́C�{�N���X�w�莞��
  // �u��������Ă��Ȃ����\�b�h�̒ǉ��v�Ń��\�b�h�X�^�u���y�Ɏ������������邽�߁B
  // �܂��C�ُ�n�̏����t���[���^���ɃR�[�f�B���O�����邽�߁B


  // �ʐM�������̏������L�q������B
  // ���O��onPostSuccess�ł͂Ȃ�onPostCompleted�ɂ������R�́C
  // ���\�b�h�����������ɐ���n���擪�ɗ���悤�ɂ��邽�߁B
  public abstract void onPostCompleted( String response );

  // �ʐM���s���̏������L�q������
  public abstract void onPostFailed( String response );

}
