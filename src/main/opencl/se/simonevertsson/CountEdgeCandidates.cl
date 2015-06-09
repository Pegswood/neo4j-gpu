__kernel void count_edge_candidates(
    int q_start_node,
    int q_end_node,
    __global int* d_adjacencies,
    __global int* d_adjacency_indicies,

    __global int* candidates_edge_counts,
    __global int* candidates_array,
    __global bool* candidate_indicators,
    int d_node_count)
{
    int workset_index = get_global_id(0);
    candidates_edge_counts[workset_index] = 0;

    int candidate_start_node = candidates_array[workset_index];

    int c_adjacency_start_index = d_adjacency_indicies[candidate_start_node];
    int c_adjacency_end_index = d_adjacency_indicies[candidate_start_node+1];

    for(int i = c_adjacency_start_index; i < c_adjacency_end_index; i++) {
        int candidate_end_node = d_adjacencies[i];
        candidates_edge_counts[workset_index] += candidate_indicators[q_end_node*d_node_count + candidate_end_node];
    }
}