package kdtrees;

public interface KDKey {
    /**
     * Get the value of this key at given dimension
     * 
     * @param dimension
     * @return value of this key at dimension 'dimension'
     */
    public double getKey(int dimension);
}
