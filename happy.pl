% find what drinks this person likes based on where they frequent and maximum they will pay for drink.
drinks(P, I, R) :- frequents(P, X), likes(P, Z), sells(X, Z, A, R, _), A =< I. 

drinks_price(P, I, R) :- drinks(P, I, Z), sells(_, _, R, Z, _).

% get rating of resteraunt 
rest_rating(P, I, R) :- frequents(P, X), likes(P, Z), sells(X, Z, A, _, B), A =< I, rates(B, R).

% get list of all statements.
all_drinks(P, I, L) :- setof(K, drinks(P, I, K), L).
all_drinks_price(P, I, L):- findall(K, drinks_price(P, I, K), L).
all_rest_rating(P, I, L):- findall(K, rest_rating(P, I, K), L).
all_resteraunts(P, I, L) :- setof(K, resteraunts(P, I, K), L).


num_drinks(P, I, N) :- all_drinks(P, I, L), length(L, N).
sum_drinks_price(P, I, R) :- all_drinks_price(P, I, Z), sum_list(Z, R).

% happy index calculation
first_part(P, I, R) :- num_drinks(P, I, Z), R is Z * 10.
second_part(P, I, R) :- sum_drinks_price(P, I, Z), num_drinks(P, I, Y), R is (I * Y) / Z.
third_part(P, I, R):- all_rest_rating(P, I, Z), sum_list(Z, R).
happy_index(P, I, R) :- first_part(P, I, A), second_part(P, I, B), third_part(P, I, C), R is A + B + C.