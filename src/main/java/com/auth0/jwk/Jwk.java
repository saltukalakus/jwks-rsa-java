package com.auth0.jwk;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Maps;
import org.apache.commons.codec.binary.Base64;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.List;
import java.util.Map;

public class Jwk {
    private static final String PUBLIC_KEY_ALGORITHM = "RSA";

    private final String id;
    private final String type;
    private final String algorithm;
    private final String usage;
    private final String operations;
    private final String certificateUrl;
    private final List<String> certificateChain;
    private final String certificateThumbprint;
    private final Map<String, Object> additionalAttributes;

    public Jwk(String id, String type, String algorithm, String usage, String operations, String certificateUrl, List<String> certificateChain, String certificateThumbprint, Map<String, Object> additionalAttributes) {
        this.id = id;
        this.type = type;
        this.algorithm = algorithm;
        this.usage = usage;
        this.operations = operations;
        this.certificateUrl = certificateUrl;
        this.certificateChain = certificateChain;
        this.certificateThumbprint = certificateThumbprint;
        this.additionalAttributes = additionalAttributes;
    }

    public static Jwk fromValues(Map<String, Object> map) {
        Map<String, Object> values = Maps.newHashMap(map);
        String kid = (String) values.remove("kid");
        String kty = (String) values.remove("kty");
        String alg = (String) values.remove("alg");
        String use = (String) values.remove("use");
        String keyOps = (String) values.remove("key_ops");
        String x5u = (String) values.remove("x5u");
        @SuppressWarnings("unchecked")
        List<String> x5c = (List<String>) values.remove("x5c");
        String x5t = (String) values.remove("x5t");
        return new Jwk(kid, kty, alg, use, keyOps, x5u, x5c, x5t, values);
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public String getUsage() {
        return usage;
    }

    public String getOperations() {
        return operations;
    }

    public String getCertificateUrl() {
        return certificateUrl;
    }

    public List<String> getCertificateChain() {
        return certificateChain;
    }

    public String getCertificateThumbprint() {
        return certificateThumbprint;
    }

    public Map<String, Object> getAdditionalAttributes() {
        return additionalAttributes;
    }

    public PublicKey getPublicKey() throws InvalidPublicKeyException {
        if (!PUBLIC_KEY_ALGORITHM.equalsIgnoreCase(type)) {
            return null;
        }
        try {
            KeyFactory kf = KeyFactory.getInstance(PUBLIC_KEY_ALGORITHM);
            BigInteger modulus = new BigInteger(1, Base64.decodeBase64(stringValue("n")));
            BigInteger exponent = new BigInteger(1, Base64.decodeBase64(stringValue("e")));
            return kf.generatePublic(new RSAPublicKeySpec(modulus, exponent));
        } catch (InvalidKeySpecException e) {
            throw new InvalidPublicKeyException("Invalid public key", e);
        } catch (NoSuchAlgorithmException e) {
            throw new InvalidPublicKeyException("Invalid algorithm to generate key", e);
        }
    }

    private String stringValue(String key) {
        return (String) additionalAttributes.get(key);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("kid", id)
                .add("kyt", type)
                .add("alg", algorithm)
                .add("use", usage)
                .add("extras", additionalAttributes)
                .toString();
    }
}
