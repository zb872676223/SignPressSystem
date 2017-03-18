package signsocket;

public enum ClientRequest {
	LOGIN_REQUEST,
	DOWNLOAD_STATISTIC_REQUEST,
	DOWNLOAD_REGULARLOAD_REQUEST,
	
	
    /// <summary>
    /// ��ѯ��ǩ��״̬����
    /// QUERY_UNSIGN_CONTRACT_REQUEST ǩ���˲�ѯ�Լ���Ҫǩ���еĻ�ǩ��
    /// QUERY_SIGNED_CONTRACT_REQUEST ǩ���˲�ѯ�Լ��Ѿ�ǩ���ֵĻ�ǩ��
    /// 
    /// INSERT_SIGN_DETAIL_REQUEST    ǩ���˽���ǩ��
    /// QUERY_SIGN_DETAIL_REQUEST     ǩ���˲�ѯ�Լ���ǩ����ϸ  
    /// QUERY_SIGN_DETAIL_CON_REQUEST ǩ���˲�ѯ�Լ������ĳ�����ӵ�ǩ����ϸ
    /// </summary>
    QUERY_UNSIGN_CONTRACT_REQUEST,
    QUERY_SIGNED_CONTRACT_REQUEST,

    INSERT_SIGN_DETAIL_REQUEST,
    QUERY_SIGN_DETAIL_REQUEST,
    QUERY_SIGN_DETAIL_CON_REQUEST,
    GET_HDJCONTRACT_REQUEST,
    
    QUERY_SIGNED_CONTRACT_TOP_REQUEST,
}