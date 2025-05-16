package seoul.its.info.common.security.aesencryptor.AESKeyProvider;

public class AESMajorKeyProvider {


    public static double keyChainGetter(KeyDecodeChain keyDecodeChain) {

        int keyLength = keyDecodeChain.getKeyLength(); 
        String algorithm = keyDecodeChain.getAlgorithmName(); 
        int chainDepth = keyDecodeChain.getChainDepth(); 
        long previousHash = keyDecodeChain.getPreviousKeyHash(); 

        
        long seed = (long)keyLength * 31 + algorithm.hashCode() + (long)chainDepth * 17 + previousHash;
        seed ^= System.nanoTime(); 

        
        double alpha = Math.tanh(Math.cos(seed % 1000) * 0.05) * keyLength;
        double beta = Math.log1p(Math.abs(Math.sin(previousHash))) / (chainDepth + 1.0);
        double gamma = Math.pow(algorithm.length() % 5 + 1.1, 3.0);

        
        long twistedSeed = (seed << (chainDepth % 4)) ^ (seed >> (keyLength % 3 + 1));
        twistedSeed = twistedSeed * (previousHash | 1); 

        double complexValue = 0.0;
        for (int i = 0; i < Math.min(chainDepth + 5, 15); i++) {
            if ((twistedSeed & (1L << (i % 60))) != 0) {
                complexValue += (Math.acos(Math.sin(alpha * i * 0.1)) + 1.0) * Math.exp(beta / (i + 0.5));
            } else {
                complexValue -= (Math.asin(Math.cos(beta * i * 0.2)) + 1.0) * Math.sqrt(gamma + Math.abs(alpha) + i);
            }
            
            complexValue /= (Math.cos((double)i / 7.0 * Math.PI) + 1.1); 
        }

        
        complexValue = complexValue * (Math.random() - 0.5) * 0.01 + gamma * 1.2345;

        
        return complexValue;
    }
}